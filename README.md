# Server Translation API
It's a library for handling translations server side. It supports
per player language, by using one provided by client on join/language change.

## Adding as dependency:
Add it to your dependencies like this:

```groovy
repositories {
	maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
	modImplementation include("xyz.nucleoid:server-translations-api:[TAG]")
}
```

For `[TAG]`/translations api version I recommend you checking [this maven](https://maven.nucleoid.xyz/xyz/nucleoid/server-translations-api/).

### For versions before 1.19.4:
```groovy
repositories {
	maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
	modImplementation include("fr.catcore:server-translations-api:[TAG]")
}
```

For `[TAG]`/translations api version I recommend you checking [this maven](https://maven.nucleoid.xyz/fr/catcore/server-translations-api/).

## Usage
To use it, you just need to use vanilla `Text.translation(...)` with key specified by you in your code. 

Then you just need to create `data/modid/lang` folder in your mod's resources.
Then you can create there `en_us.json` for default translation and similar files for other languages (same format as vanilla translations).

Example valid language file looks like this:
```
{
  "block.honeytech.pipe": "Pipe",
  "block.honeytech.item_extractor": "Item Extractor",
  "block.honeytech.trashcan": "Trash Can",
  "block.honeytech.cable": "Cable",
  "item.honeytech.diamond_dust": "Diamond Dust",
  "item.honeytech.raw_aluminium": "Raw Aluminium Ore",
  "item.honeytech.aluminium_ingot": "Aluminium Ingot",
  "item.honeytech.copper_wire": "Copper Wire",
  "item.honeytech.motor": "Motor",
  "gui.honeytech.show_recipes": "Show Recipes"
}
```
