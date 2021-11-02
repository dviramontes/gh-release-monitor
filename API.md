# `gh-release-monitor/API.md`

## HealthCheck

### GET /ping
```shell
curl "http://localhost:4000/ping"
```

## Repo

### GET /api/repos
Returns a list of all tracked repos
```shell
curl "http://localhost:4000/api/repos"
```

### GET /api/repos/search
Search against [Github's repo rest API](https://docs.github.com/en/rest/reference/search#search-repositories)

Query Params:
- `q` required / string
- `per-page` optional / int / default: 1
```shell
curl "http://localhost:4000/api/search?q=honeysql&per-page=1"
```


