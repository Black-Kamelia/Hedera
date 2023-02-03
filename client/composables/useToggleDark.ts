export default function useToggleDark(): (value?: boolean) => void {
  return useToggle($$($isDark))
}
