# modus

modus monolith backend with clojure and postgres

## Installation (Instruction for Chi - or noob like Chi)

* Install postgres: http://postgresapp.com/
* Install leiningen: http://leiningen.org/#install

## Usage

* Recreate the whole database: ./bootstrap.sh (no longer run backend)
* Migration and run backend: ./run.sh
* Just wanna run the backend: lein run -m modus.main/-main
* Dev: use repl - of course

## API docs
* locally: http://localhost:3000/api-docs