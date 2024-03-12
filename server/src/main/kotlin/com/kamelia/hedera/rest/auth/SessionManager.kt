package com.kamelia.hedera.rest.auth

/*
object SessionManager {

    private val LOGGER = KtorSimpleLogger("SessionManager")
    private val PURGE_INTERVAL = 5.minutes

    private val mutex = Mutex()

    // accessToken -> session
    private val sessions = mutableMapOf<String, Session>()
    // refreshToken -> access+refresh (tokendata)
    private val refreshTokens = mutableMapOf<String, Session>()
    // userId -> userState
    private val loggedUsers = mutableMapOf<UUID, UserState>()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var pruneJob: Job? = null

    fun startPruning() {
        if (pruneJob != null) return
        pruneJob = coroutineScope.launchPeriodic(PURGE_INTERVAL) {
            val now = System.currentTimeMillis()
            var sessionCount = 0
            var refreshTokenCount = 0
            mutex.withReentrantLock {
                sessions.entries.removeIf {
                    val b = it.value.tokenData.accessTokenExpiration < now
                    if (b) sessionCount++
                    b
                }
                loggedUsers.entries.removeIf {
                    sessions.values.none { session -> session.user.uuid == it.key }
                }
                refreshTokens.entries.removeIf {
                    val b = it.value.refreshTokenExpiration < now
                    if (b) refreshTokenCount++
                    b
                }
            }
            if (sessionCount > 0) LOGGER.info("Pruned $sessionCount sessions")
            if (refreshTokenCount > 0) LOGGER.info("Pruned $refreshTokenCount refresh tokens")
        }
    }

    fun stopPruning() = coroutineScope.launch {
        check(pruneJob != null) { "Pruning not started" }
        pruneJob?.cancel()
        pruneJob = null
    }

    private suspend fun generateTokens(
        user: User,
        sessionId: UUID? = null,
        refreshToken: String? = null
    ): Session = mutex.withReentrantLock {
        require((sessionId == null) xor (refreshToken == null)) { "Either sessionId or token must be provided" }

        val userState = loggedUsers.computeIfAbsent(user.id.value) {
            UserState(
                user.id.value,
                user.username,
                user.email,
                user.role,
                user.enabled,
                user.forceChangePassword,
                user.currentDiskQuota,
                user.maximumDiskQuota,
                user.createdAt
            )
        }
        val tokenData = Session.from(user)

        if (sessionId == null) {
            val oldTokens = refreshTokens.remove(refreshToken) ?: throw ExpiredOrInvalidTokenException()
            val session = sessions.remove(oldTokens.accessToken) ?: throw ExpiredOrInvalidTokenException()
            sessions[tokenData.accessToken] = session.copy(user = userState, tokenData = tokenData)
        } else {
            val session = Session(sessionId, userState, tokenData)
            sessions[tokenData.accessToken] = session
        }

        refreshTokens[tokenData.refreshToken] = tokenData
        tokenData
    }

    suspend fun updateSession(userId: UUID, user: User): Unit = mutex.withReentrantLock {
        val (currentDiskQuota, maximumDiskQuota) = user.getDiskQuota()
        loggedUsers[userId]?.apply {
            username = user.username
            email = user.email
            role = user.role
            enabled = user.enabled
            forceChangePassword = user.forceChangePassword
            this.currentDiskQuota = currentDiskQuota
            this.maximumDiskQuota = maximumDiskQuota
        }

        if (!user.enabled) {
            logoutAll(user, "disabled")
        } else {
            UserEvents.userUpdatedEvent(user.toRepresentationDTO())
        }
    }

    suspend fun getUserOrNull(userId: UUID): UserState? = mutex.withReentrantLock {
        loggedUsers[userId]
    }

    suspend fun login(username: String, password: String): Response<SessionOpeningDTO> {
        val unauthorized = Response.unauthorized(Errors.Auth.INVALID_CREDENTIALS)
        val user = User.findByUsername(username) ?: return unauthorized
        val sessionId = UUID.randomUUID()

        if (!Hasher.verify(password, user.password).verified) {
            delay(Environment.loginThrottle)
            return unauthorized
        }

        if (!user.enabled) {
            return Response.forbidden(Errors.Auth.ACCOUNT_DISABLED)
        }

        return Response.created(SessionOpeningDTO(
            sessionId = sessionId,
            tokens = generateTokens(user, sessionId = sessionId),
            user = user.toRepresentationDTO(),
            userSettings = user.settings.toRepresentationDTO()
        ))
    }

    suspend fun refresh(jwt: Payload, token: String): Response<Session> {
        val user = User.findByUsername(jwt.subject) ?: throw ExpiredOrInvalidTokenException()
        return Response.created(generateTokens(user, refreshToken = token))
    }

    suspend fun verify(token: String): Session? = mutex.withReentrantLock {
        sessions[token]
    }

    suspend fun verifyRefresh(token: String): Unit = mutex.withReentrantLock {
        refreshTokens[token] ?: throw ExpiredOrInvalidTokenException()
    }

    suspend fun logout(token: String): Unit = mutex.withReentrantLock {
        val session = sessions.remove(token) ?: throw ExpiredOrInvalidTokenException()
        refreshTokens.remove(session.tokenData.refreshToken)
    }

    suspend fun logoutAll(user: User, reason: String = "logout_all") = mutex.withReentrantLock {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(
                userId = user.id.value,
                reason = reason,
            )
        )
        sessions.entries.filter {
            it.value.user.uuid == user.id.value
        }.forEach {
            refreshTokens.entries.removeIf { refreshEntry ->
                refreshEntry.value.accessToken == it.key
            }
            sessions.remove(it.key)
        }
        loggedUsers.remove(user.id.value)
    }

    suspend fun logoutAllExceptCurrent(user: User, token: String, sessionId: UUID, reason: String) = mutex.withReentrantLock {
        UserEvents.userForcefullyLoggedOutEvent(
            UserForcefullyLoggedOutDTO(
                userId = user.id.value,
                reason = reason,
            ),
            ignoredSessions = listOf(sessionId)
        )
        sessions.entries.filter {
            it.value.user.uuid == user.id.value && it.key != token
        }.forEach {
            refreshTokens.entries.removeIf { refreshEntry ->
                refreshEntry.value.accessToken == it.key
            }
            sessions.remove(it.key)
        }
    }
}

data class UserState(
    val uuid: UUID,
    var username: String,
    var email: String,
    var role: UserRole,
    var enabled: Boolean,
    var forceChangePassword: Boolean,
    var currentDiskQuota: Long,
    var maximumDiskQuota: Long,
    val createdAt: Instant,
) : Principal {

    fun toUserRepresentationDTO() = UserRepresentationDTO(
        uuid,
        username,
        email,
        role,
        enabled,
        forceChangePassword,
        currentDiskQuota,
        if(maximumDiskQuota > 0L) currentDiskQuota.toDouble() / maximumDiskQuota.toDouble() else 0.0,
        maximumDiskQuota,
        createdAt.toString(),
    )
}

data class Session(
    val sessionId: UUID,
    val user: UserState,
    val tokenData: Session,
)
 */
