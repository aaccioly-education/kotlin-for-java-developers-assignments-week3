package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        allDrivers - trips.map { it.driver }.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        nTripsPerPassenger(minTrips).keys

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        nTripsPerPassenger(2) { trip -> trip.driver == driver }.keys

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    // alternatively could have been implemented with partition
    val tripsWithDiscount = nTripsPerPassenger { trip -> trip.discount != null }
    val tripsWithoutDiscount = nTripsPerPassenger { trip -> trip.discount == null }

    return tripsWithDiscount.filter { (p, nTripsWithDiscount) ->
        tripsWithoutDiscount.getOrDefault(p, 0) < nTripsWithDiscount
    }.keys
}


/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    return trips.groupingBy { it.duration / 10 }
            .eachCount()
            .maxBy { (_, nTrips) -> nTrips }
            ?.run { key * 10 until key * 10 + 10 }
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (trips.isEmpty())
        return false

    val totalIncome = trips.map { it.cost }.sum()
    val incomePerDriver = trips.groupingBy { t -> t.driver }
            .fold(0.0) { acc, trip -> acc + trip.cost }
    // 20% of the drivers
    val nTopDrivers = (allDrivers.size * 0.2).toInt()
    val topDriversIncome = incomePerDriver.values.sortedDescending().take(nTopDrivers).sum()

    return topDriversIncome >= totalIncome * 0.8
}


private fun TaxiPark.nTripsPerPassenger(minTrips: Int = 0, pred: (Trip) -> Boolean = { true }): Map<Passenger, Int> {
    val nTripsPerPassenger = allPassengers.associate { p ->
        p to trips.count { t ->
            t.passengers.contains(p) && pred(t)
        }
    }

    return nTripsPerPassenger.filterValues { nTrips -> nTrips >= minTrips }
}