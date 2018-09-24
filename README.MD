# Anime Image Sorter
Tool for automatic tagging and sorting of anime images

Use ```mvn exec:java -h``` for help.

## Filters
Each directory in sorting destination should contain .filter file to specify rules of sorting. This file is array 
with possibly multiple sorting rules. Rule in the array are joined by logical AND. Subdirectories inherit rules from 
their roots.
Each rule have priority, bigger is better!

### All
Matches all images
```json
{
    "type": "ALL",
    "priority": -10,
}
```

### NONE
Matches no image
```json
{
    "type": "NONE",
    "priority": 100,
}
```

### TAG
Matches tag from category COPYRIGHT with value love_hina. For categories check [Java Iqdb API](https://github.com/GreenManSK/Java-IqdbApi)
```json
{
    "type": "TAG",
    "priority": 100,
    "tagType": "COPYRIGHT",
    "tagValue": "love_hina"
}
```

### MULTIPE_TAG
Matches if image has any of the tags (split by ,) in this category.
```json
{
    "type": "MULTI_TAG",
    "priority": 15,
    "tagType": "GENERAL",
    "tagValue": "santa_boots,santa_claus_costume"
}
```

### MULTIPE_TAG_AND
Matches only is image has all of the mention tags.
```json
{
    "type": "MULTI_TAG_AND",
    "priority": 15,
    "tagType": "GENERAL",
    "tagValue": "santa_boots,santa_claus_costume"
}
```

### MULTIPE_TAG_ONLY
Matches only if ALL tags of image in the category are mentioned in the rule.
```json
{
    "type": "MULTI_TAG_ONLY",
    "priority": 150,
    "tagType": "CHARACTER",
    "tagValue": "nishikino_maki"
}
```