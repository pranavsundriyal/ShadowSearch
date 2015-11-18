# ShadowSearch
Makes call to expedia mobile api using a spring boot app, uses Lamdas as modifiers.

To run :
./gradlew bootRun

From browser :
localhost:9191/search?origin=MIA&dest=LAS&departure=2015-12-20&arrival=2015-12-30&function=multiply&param=1
Fires of one query to expedia with origin "MIA (miami)" and destination "LAS (las vegas)" with departure date on "2015-12-20", for roundtrips add param "arrival=2016-01-16".

Functions supported

- changeDepartureAheadDays (fire searches for 'n' days ahead of original departure date defined by param)
- changeDepartureBehindDays (fire searches for 'n' days behind of original departure date defined by param)
- flexSearchRT (fire searches for 'n' days flex search defined by param creating a matrix)
- flexSearchOW (fire searches for 'n' days flex search defined by param for one way searches)
- flexSearchOWArrival (fire searches with 'n' flexible arrival dates ahead and behind the original date, defined by param)
- multiply (clones a user request n times defined by param)
- changeMaxOffer (change number of results returned using param, default is 2000)
