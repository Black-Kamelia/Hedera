import type { StoreDefinition } from '@pinia/nuxt/dist/runtime/composables'

/**
 * Wraps a useStore composable with a new composable which returns a tuple of the store and its refs.
 * @param useStore The useStore composable to wrap.
 * @returns A new composable which returns a tuple of the store and its refs. ([refs, store])
 */
export default function s<R>(useStore: StoreDefinition<any, any, any, any>): () => R {
  return function useDestructurableStore(): R {
    const store = useStore()
    const refs = storeToRefs(store)
    return {
      ...store,
      ...refs,
    } as R
  }
}
