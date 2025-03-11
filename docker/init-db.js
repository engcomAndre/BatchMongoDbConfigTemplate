print("########################################## CREATING DB ########################################################")

db = db.getSiblingDB('batchProcess');
db.createCollection('batchProcesss');

JSON.stringify(db)

print("########################################## DB CONFIGURATION FINISHED ##########################################")
sleep(3000);
