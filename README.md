# Restaurants voting system

## Description
* 2 types of users: admin and regular users
* Admin can input a restaurant, and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote for a restaurant they want to have lunch at today
* Only one vote counted per user
* If user votes again the same day:
  * If it is before 11:00 we assume that he changed his mind
  * If it is after 11:00 then it is too late, vote can't be changed
* Each restaurant provides a new menu each day.

## Tech stack
* Java 21
* Spring Boot
* Spring MVC
* Spring Data
* Spring Security
* Spring Cache
* Maven
* JPA(Hibernate)
* REST(Jackson)
* H2 DB
* JUnit

## API Documentation
http://localhost:8080/swagger-ui/index.html#/

### User Profile
Get user details: *GET /profile* \
Register a new user: *POST /profile* \
Change user data: *PUT /profile*

### User Administration
Get users list: *GET /admin/users* \
Create a new user: *POST /admin/users* \
Get user details: *GET /admin/users/{id}* \
Change user data: *PUT /admin/users/{id}* \
Delete user: *DELETE /admin/users/{id}*

### Restaurants voting
Get restaurants list and their votes count: *GET /profile/restaurants* \
Get menu for today: *GET /profile/restaurants/{id}/menu* \
Vote for a restaurant: *PUT /profile/restaurants/{id}/vote*

### Restaurants Management
Get restaurants list and their votes count: *GET /admin/restaurants* \
Create a new restaurant: *POST /admin/restaurants* \
Get restaurant details: *GET /admin/restaurants/{id}* \
Change restaurant details (if no votes for today): *PUT /admin/restaurants/{id}* \
Delete restaurant (if no votes for today): *DELETE /admin/restaurants/{id}* 

### Menu Management
Get menu for today: *GET /admin/restaurants{id}/menu* \
Add a new dish: *POST /admin/restaurants/{id}/menu* \
Get menu for dates: *GET /admin/restaurants{id}/menu?filter?startDate={startDate}&endDate={endDate}* \
Get dish details: *GET /admin/restaurants{id}/menu/{id}* \
Change dish details (if no votes for today): *PUT /admin/restaurants{id}/menu/{id}* \
Delete dish (if no votes for today): *DELETE /admin/restaurants{id}/menu/{id}* \
