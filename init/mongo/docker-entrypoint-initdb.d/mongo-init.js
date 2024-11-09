print('START');

// get access to product-service database
db = db.getSiblingDB('booking-service');

db.createUser(
    {
        user: 'admin',
        pwd: 'password',
        roles: [ {role: 'readWrite', db: 'booking-service'}],
    },
);

db.createCollection('bookings');

print('END');a