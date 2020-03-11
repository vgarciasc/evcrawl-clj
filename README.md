# evcrawl-clj

A simple Clojure/ClojureScript crawler for the brazilian book website "Estante Virtual".

This already existed in [Python](https://github.com/vgarciasc/estante-virtual-minicrawler), but I decided to remake it so I could learn Clojure and ClojureScript.

## Structure

This repository is divided in two projects:
1. *evcrawl* (Clojure): has two modes:
  - `-script`: fetches new prices and notifies the user via Pushbullet.
  - `-server`: runs the back-end for *encrud*. 
2. *encrud* (ClojureScript): displays a simple CRUD for the watched books.

## How do I use this?

### Setting up

1. Get the latest release.
2. Run the standalone `.jar` in *script* mode:
  - `java -jar evcrawl-x.y.z-standalone.jar -script`
3. Fill the newly-created `pb-token.txt` with your Pushbullet API key.

### Adding books

1. Run the `.jar` in *server* mode:
  - `java -jar evcrawl-x.y.z-standalone.jar -server`
2. Open the provided `index.html`.
3. Add the books you want to be notified about.

### Fetching prices

1. Run the `.jar` in *script* mode:
  - `java -jar evcrawl-x.y.z-standalone.jar -script`
  - NOTE: if you haven't set up your Pushbullet API key correctly, you won't be notified of price changes.
