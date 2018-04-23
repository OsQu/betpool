package fixtures

const val marketsApiResponse = """
[
  {
    "marketId": "1.142872972",
    "event": "Mark Allen v Liam Highfield",
    "start": "2018-04-22T09:00:00.000Z",
    "odds": {
      "2279947": {
        "name": "Mark Allen",
        "odds": 1.31
      },
      "4392732": {
        "name": "Liam Highfield",
        "odds": 4
      }
    }
  },
  {
    "marketId": "1.142873360",
    "event": "Shaun Murphy v Jamie Jones",
    "start": "2018-04-22T13:30:00.000Z",
    "odds": {
      "2278789": {
        "name": "Shaun Murphy",
        "odds": 1.4
      },
      "3502366": {
        "name": "Jamie Jones",
        "odds": 3.4
      }
    }
  }
]
"""

const val flowdockResponse = """
{}
"""