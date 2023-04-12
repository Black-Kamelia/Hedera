import type {
  StateTree,
  StoreDefinition,
  _ActionsTree,
  _GettersTree,
} from '@pinia/nuxt/dist/runtime/composables'

/**
 * Wraps a useStore composable with a new composable which returns a tuple of the store and its refs.
 * @param useStore The useStore composable to wrap.
 * @returns A new composable which returns a tuple of the store and its refs. ([refs, store])
 */
export default function s<
  Id extends string,
  S extends StateTree = {},
  G extends _GettersTree<S> = {},
  A extends _ActionsTree = {},
>(useStore: StoreDefinition<Id, S, G, A>) {
  return function useDestructurableStore() {
    const store = useStore()
    const refs = storeToRefs(store)
    return [refs, store] as const
  }
}
