import re

ROOT_CSS_VARIABLES_REGEX = r":root\s*{\s*(.*?)\s*}"


def get_candidate_or_none(variables: dict, value: str):
    if value not in variables.values():
        return None

    val = None
    for k, v in variables.items():
        if v == value:
            if val is not None:
                return None
            val = k
    return f"var(--{val})"


def replace_color_values(match, variables):
    value = match.group(1)

    new_value = get_candidate_or_none(variables, value)
    if new_value is None:
        return match.group(0)

    return match.group(0).replace(value, new_value).strip()


def patch_theme(content: str):
    # Parsing CSS variables in root element
    variables = re.findall(r":root\s*{\s*(.*?)\s*}", content, re.DOTALL)
    variables = dict(re.findall(r'--([\w-]+):\s*([^\n;]+);', variables[0]))

    print(f"🔎 Found {len(variables)} variables")

    # Search for hardcoded colors and replace them with variables
    print("🎨 Replacing hardcoded colors with variables...", end='')
    css_content_modified = re.sub(r'[\w-]+:\s*(#[^\n;]+);', lambda match: replace_color_values(match, variables), content)
    print(" OK")

    print("📐 Replacing hardcoded border-radius with variable...", end='')
    css_content_modified = re.sub(r'border-radius:\s*6px;', r'border-radius: var(--border-radius);', css_content_modified)
    print(" OK")

    return css_content_modified


print("""
    __  __         __               
   / / / /__  ____/ /__  _________ _
  / /_/ / _ \/ __  / _ \/ ___/ __ `/
 / __  /  __/ /_/ /  __/ /  / /_/ / 
/_/ /_/\___/\__,_/\___/_/   \__,_/  
                                    
Theme patcher v1.0
""")

original_theme = input("Path to original theme: ")
print("📖 Opening original theme...")
with open(original_theme, 'r') as file:
    css_content = file.read()
print()

print("✨ Patching theme...")
patched_theme = patch_theme(css_content)
print()

patched_theme_path = original_theme.replace('.css', '_patched.css')
print(f"🖋️ Writing theme to {patched_theme_path}...")
with open(patched_theme_path, 'w') as file:
    file.write(patched_theme)
print()

print("Done.")
