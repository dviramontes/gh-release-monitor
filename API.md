# `gh-release-monitor/API.md`

## Health Check

### GET /ping
```shell
curl "http://localhost:4000/ping"
```
response:
```
pong
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
response:
```json
{
  "query": "honeysql", "repos": [...], "per-page": 1
}
```

## Releases

### GET /api/releases
Returns a list of all tracked releases
```shell
curl "http://localhost:4000/api/releases"
```

response:
```json
{
  "releases": [...]
}
```

### POST /api/releases/follow/:owner/:repo
Creates a new release to follow
```shell
curl "http://localhost:4000/api/releases/follow/facebook/react"
```
response:
```json
{"owner":"facebook","repo":"react","release":{"id":1,"created_at":"2021-11-02T05:35:39Z","updated_at":"2021-11-02T05:35:39Z","deleted_at":null,"owner":"facebook","repo":"c","body":""}}
```

### DELETE /api/releases/unfollow/:id
Removes a release to follow from db
```shell
curl "http://localhost:4000/api/releases/unfollow/1"
```
response:
```
release id: 1 successfully removed
```

### GET /api/releases/latest/:owner/:repo
Returns the latest release for a given repo
```shell
curl "http://localhost:4000/api/releases/facebook/react"
```
response:
```json
{"owner":"facebook","repo":"react","latest":[...]}
```
