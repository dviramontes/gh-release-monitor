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

### Requirements

- Java8+
- Clojure
- Docker

### Setup

##### config setup
1. `touch secrets.edn` (gitignored)
2. add your github token in this format
```clojure
{:github-token "top-secret"}
```

### Development

1. `make up` # spins up docker-compose
2. `make run` # runs code locally

#### Additional dev tasks
- `make lint` # lints code via clj-kondo
- `make format` # formats code via cljfmt
- `make repl` # spins up rebel-readline

### Production

- `make jar`

### Test

- `make test` # runs integration tests
