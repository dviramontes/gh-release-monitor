# `gh-release-monitor`

> A restful API project for monitoring github releases


### Project Layout

```
{root}

dev
└── user.clj            # utilities for repl driven development (see make repl)
src
└── api
    ├── github.clj      # functions for interacting with github API
    ├── main.clj        # app's main entry and middleware configurations       
    ├── db.clj          # DB config details
    ├── handlers.clj    # handlers that interact with the DB       
    └── routes.clj      # API routes
```

- [For a list of endpoints supported by this API refer to this doc.](./API.md)
- [For this project's KANBAN board follow this link.](https://github.com/dviramontes/gh-release-monitor/projects/1)

### Requirements

- Java8+
- Clojure
- Docker

### Setup

##### config setup
1. `touch secrets.edn` (.gitignored)
2. add your github token in this format
```clojure
{:github-token "top-secret"}
```

### Development

1. `make up` # spins up docker-compose
2. `make run` # runs code locally

#### Running with REPL

1. `make repl` # runs nrepl 
2. API will now start serving traffic on `localhost:4000`
3. evaluate `(reset)` after changing code
4. evaluate `(halt)` to stop the server or simply `CTRL + c` to end the process

#### Additional dev tasks

- `make lint` # lints code via clj-kondo
- `make format` # formats code via cljfmt
- `make repl` # spins up rebel-readline

### Production

- `make jar`

### Test

- `make test` # runs integration tests
