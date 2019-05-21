# Badminton Scheduler

Android app for badminton scheduling.

For Slackbot backend, see [Plutoniummatt/bb](https://github.com/plutoniummatt/bb)

For API backend, see [Haloyum/bab-server](https://github.com/Haloyum/bab-server)

In order to build the app, you need to define a string called `api_base_url`, which
is the base URL that Retrofit will use to construct its paths.

### Working API definition
```
GET courts
{
  "reservations": [
    {
      "token": String,
      "courtNumber": Int,
      "playerNames": [String],
      "startAt": Date,
      "randoms": Boolean
    }
  ]
}

POST courts/register
{
  "courtNumber": Int,
  "players": [String],
  "delayInMinutes": Int,
  "randoms": Boolean
}

POST courts/unregister
{
  "token": String
}

POST courts/reset
{
  "courtNumber": Int
}

///////////////////////////////////////
//  Player-related endpoints         //
///////////////////////////////////////

GET players
{
  "players": [
    {
      "name": String,
      "password": String,
      "courtNumber": Int?
    }
  ]
}

POST players/add
{
  "name": String,
  "password": String
}

POST players/delete
{
  "name": String
}

///////////////////////////////////////
//  Session-related endpoints        //
///////////////////////////////////////

POST sessions

DELETE sessions
```
