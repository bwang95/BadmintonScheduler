# Badminton Scheduler
Android app for badminton scheduling.

For API and Slackbot backend, see [Plutoniummatt/bb](https://github.com/plutoniummatt/bb)

In order to build the app, you need to define a string called `api_base_url`, which
is the base URL that Retrofit will use to construct its paths.

### Working API definition
```
GET courts
{
    "courts": [
        {
            "court_number": Int,
            "registrations": [
                {
                    "start_time_seconds": Long,
                    "names": [String]
                }
            ]
        }
    ]
}

POST courts/register
{
    "court_number": Int,
    "names": [String],
    "delay_time_minutes": Int,
    "randoms": Boolean
}

POST courts/unregister
{
    "court_number": Int
}

POST courts/reset
{
    "court_number": Int
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
            "court_number": Int?
        }
    ]
}

POST players
{
    "name": String,
    "password": String
}

DELETE players/{name}

///////////////////////////////////////
//  Session-related endpoints        //
///////////////////////////////////////

POST sessions

DELETE sessions
```
