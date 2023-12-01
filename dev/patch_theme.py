import re

ROOT_CSS_VARIABLES_REGEX = r":root\s*\{[^}]*}"
FONT_FACE_REGEX = r"@font-face\s*\{[^}]*}"


# def get_candidate_or_none(variables: dict, value: str):
#     if value not in variables.values():
#         return None
#
#     val = None
#     for k, v in variables.items():
#         if v == value:
#             if val is not None:
#                 return None
#             val = k
#     return f"var(--{val})"


def get_first_candidate_or_none(variables: dict, value: str):
    if value not in variables.values():
        return None

    for k, v in variables.items():
        if v == value:
            return f"var(--{k})"
    return None


def replace_color_values(match, variables):
    value = match.group(1)

    new_value = get_first_candidate_or_none(variables, value)
    if new_value is None:
        return match.group(0)

    return match.group(0).replace(value, new_value).strip()


def patch_colors(content: str, variables: dict):
    print("✨ Replacing hardcoded colors with variables...", end='')
    css_content_modified = re.sub(r'[\w-]+:\s*.*(#[^\n;]+).*;', lambda match: replace_color_values(match, variables), content)
    print(" OK")
    return css_content_modified


def patch_focus_ring(content: str, variables: dict):
    print("✨ Replacing hardcoded focus ring with variables...", end='')
    css_content_modified = re.sub(fr'box-shadow:\s*({variables["focus-ring"]});', r'box-shadow: var(--focus-ring);', content)
    print(" OK")
    return css_content_modified


def patch_border_radius(content: str):
    print("✨ Replacing hardcoded border-radius with variable...", end='')
    css_content_modified = re.sub(r'border-radius:\s*6px;', r'border-radius: var(--border-radius);', content)
    print(" OK")
    return css_content_modified


def patch_root(content: str):
    print("✨️ Replacing root element ...", end='')
    css_content_modified = re.sub(ROOT_CSS_VARIABLES_REGEX, '', content, re.DOTALL)

    css_content_modified = ":root {\n\tcolor-scheme: light;\n}" + css_content_modified

    print(" OK")
    return css_content_modified


def patch_font_face(content: str):
    print("️✨ Removing font-face ...", end='')
    css_content_modified = re.sub(FONT_FACE_REGEX, '', content, re.DOTALL)
    print(" OK")
    return css_content_modified


def remove_comments(content: str):
    print("✨️ Removing comments ...", end='')
    css_content_modified = re.sub(r'/\*[^*]*\*+(?:[^/*][^*]*\*+)*/', '', content, re.DOTALL)
    print(" OK")
    return css_content_modified


def patch_theme(content: str):
    # Parsing CSS variables in root element
    variables = re.findall(ROOT_CSS_VARIABLES_REGEX, content, re.DOTALL)
    variables = dict(re.findall(r'--([\w-]+):\s*([^\n;]+);', variables[0]))

    print(f"🔎 Found {len(variables)} variables")

    theme_modified = content

    theme_modified = patch_colors(theme_modified, variables)
    theme_modified = patch_focus_ring(theme_modified, variables)
    theme_modified = patch_border_radius(theme_modified)
    theme_modified = patch_root(theme_modified)
    theme_modified = patch_font_face(theme_modified)
    theme_modified = remove_comments(theme_modified)

    return theme_modified


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

print("⚙️ Patching theme...")
patched_theme = patch_theme(css_content)
print()

patched_theme_path = original_theme.replace('.css', '_patched.css')
print(f"🖨️️ Writing theme to {patched_theme_path}...")
with open(patched_theme_path, 'w') as file:
    file.write(patched_theme)
print()

print("Done.")
