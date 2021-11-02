# `gh-release-monitor/API.md`

## Health Check

### GET /ping
```shell
curl "http://localhost:4000/ping"
```

## Repos

### GET /api/repos/search
Search against [Github's repo rest API](https://docs.github.com/en/rest/reference/search#search-repositories)

Query Params:
- `q` required / string
- `per-page` optional / int / default: 1
```shell
curl "http://localhost:4000/api/repos/search?q=honeysql&per-page=1"
```

## Releases

### GET /api/releases
Returns a list of all tracked releases
```shell
curl "http://localhost:4000/api/releases"
```

### POST /api/releases/follow/:owner/:repo
Creates a new release to follow
```shell
curl "http://localhost:4000/api/releases/facebook/react"
```

### GET /api/releases/latest/:owner/:repo
Returns the latest release for a given repo
```shell
curl "http://localhost:4000/api/releases/facebook/react"
```
