import os

def generate_tree(directory, prefix=""):
    """
    Génère l'arborescence d'un répertoire.
    
    Args:
        directory (str): Le chemin du répertoire à analyser.
        prefix (str): Préfixe pour l'affichage hiérarchique.

    Returns:
        None: Affiche directement l'arbre dans la console.
    """
    try:
        entries = os.listdir(directory)
        entries.sort()
    except PermissionError:
        print(f"{prefix}[Permission Denied]")
        return

    entries_count = len(entries)
    for index, entry in enumerate(entries):
        path = os.path.join(directory, entry)
        connector = "└── " if index == entries_count - 1 else "├── "
        print(f"{prefix}{connector}{entry}")

        # Si l'entrée est un répertoire, on explore son contenu
        if os.path.isdir(path):
            extension = "    " if index == entries_count - 1 else "│   "
            generate_tree(path, prefix + extension)

if __name__ == "__main__":
    # Remplacez "." par le chemin du répertoire que vous voulez explorer
    root_directory = "."
    print(root_directory)
    generate_tree(root_directory)
