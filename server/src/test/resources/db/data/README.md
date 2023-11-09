# Fake data for testing

This directory contains fake data for testing purposes. Each record uses a custom pattern in its unique ID, described below.

## File IDs

Each file has a unique ID of the form `Aaaaaaaa-bbbb-0000-cccc-dddddddddddd` where:

- `A` is either `0` or `1` depending on whether the file belongs to a real user or a dummy user
- `aaaaaaa` is the user number
- `bbbb` is the test suite number
- `cccc` is the test number
- `dddddddddddd` is the test case number

### Examples

```
Real user's file for test 1, case 3:
00000002-0000-0000-0001-000000000003

Dummy user's file for user number 4, suite 1, test 2, case 1:
10000004-0001-0000-0002-000000000001
```

*Note: `f` is often used for test suite regarding guest users.*

## File codes

Each file has a unique code of the form `$Aabcccdddd` where:

- `A` is either `0` or `1` depending on whether the file belongs to a real user or a dummy user
- `a` is the user number
- `b` is the test suite number
- `ccc` is the test number
- `dddd` is the test case number

The codes are linked to the IDs by the following formula:

```
Code = $13f0010001

  10000003-000f-0000-0001-000000000001
$ 1      3    f       001         0001
```

## User IDs

Each user has a unique ID of the form `aaaaaaaa-bbbb-0000-cccc-dddddddddddd` where:

- `aaaaaaaa` is either all `f` or all `0` depending on whether the user is a real user or a dummy user
- `bbbb` is the test suite number
- `cccc` is the test number
- `dddddddddddd` is the test case number

### Examples

```
Real user performing action in suite 1, test 1, single case:
ffffffff-0001-0000-0001-000000000000

Dummy user needed for suite 1, test 5, case 3:
00000000-0001-0000-0005-000000000003
```

## User settings IDs

Each user settings has a unique ID of the form `aaaaaaaa-bbbb-0000-cccc-dddddddddddd` where:

- `aaaaaaaa` is either all `f` or all `0` depending on whether the user is a real user or a dummy user
- `bbbb` is the test suite number
- `cccc` is the test number or zeros
- `dddddddddddd` is the test case number or zeros
