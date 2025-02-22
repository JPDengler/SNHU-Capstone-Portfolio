import logging
import os  # For environment variables
from pymongo import MongoClient
from pymongo.errors import PyMongoError

# Configure logging
logging.basicConfig(
    filename='crud_operations.log',
    level=logging.ERROR,
    format='%(asctime)s - %(levelname)s - %(message)s'
)

class AnimalShelter:
    """CRUD operations for Animal collection in MongoDB"""

    def __init__(self, collection_name='animals'):
        # Connection Variables
        USER = os.getenv('MONGO_USER')  # Load from environment variable
        PASS = os.getenv('MONGO_PASS')  # Load from environment variable
        HOST = '127.0.0.1'
        PORT = 27017
        DB = 'AAC'
        COL = collection_name  # Use the specified collection name

        # Ensure environment variables are set
        if not USER or not PASS:
            raise EnvironmentError("MongoDB credentials are not set in the environment variables.")

        # Initialize Connection
        try:
            self.client = MongoClient(f'mongodb://{USER}:{PASS}@{HOST}:{PORT}/?authSource={DB}')
            self.database = self.client[DB]
            self.collection = self.database[COL]
            print(f"Connected to MongoDB collection: {COL}")
        except PyMongoError as e:
            logging.error(f"Error connecting to MongoDB: {e}")
            raise ConnectionError("Failed to connect to MongoDB. Check your credentials and server settings.")

    def create(self, data):
        """
        Insert a document into the specified collection.
        :param data: A dictionary containing key/value pairs to insert
        :return: True if successful insert, else False
        """
        if data is not None and isinstance(data, dict):
            try:
                result = self.collection.insert_one(data)
                print(f"Document inserted with ID: {result.inserted_id}")
                return True if result.inserted_id else False
            except PyMongoError as e:
                logging.error(f"Error inserting document: {e}")
                return False
        else:
            raise ValueError("Data must be a non-empty dictionary")

    def read(self, query):
        """
        Query for documents in the specified collection.
        :param query: A dictionary containing key/value pairs to use for lookup
        :return: A list of documents matching the query, or an empty list if no matches found
        """
        if query is not None and isinstance(query, dict):
            try:
                cursor = self.collection.find(query)
                result = list(cursor) if cursor else []
                print(f"Query returned {len(result)} documents.")
                return result
            except PyMongoError as e:
                logging.error(f"Error querying documents: {e}")
                return []
        else:
            raise ValueError("Query must be a non-empty dictionary")

    def update(self, query, new_values):
        """
        Update documents in the specified collection.
        :param query: A dictionary containing key/value pairs to use for lookup
        :param new_values: A dictionary containing key/value pairs for the new values
        :return: The number of documents updated
        """
        if query is not None and isinstance(query, dict) and new_values is not None and isinstance(new_values, dict):
            try:
                result = self.collection.update_many(query, {"$set": new_values})
                print(f"Updated {result.modified_count} documents.")
                return result.modified_count
            except PyMongoError as e:
                logging.error(f"Error updating documents: {e}")
                return 0
        else:
            raise ValueError("Query and new values must be non-empty dictionaries")

    def delete(self, query):
        """
        Delete documents in the specified collection.
        :param query: A dictionary containing key/value pairs to use for lookup
        :return: The number of documents deleted
        """
        if query is not None and isinstance(query, dict):
            try:
                result = self.collection.delete_many(query)
                print(f"Deleted {result.deleted_count} documents.")
                return result.deleted_count
            except PyMongoError as e:
                logging.error(f"Error deleting documents: {e}")
                return 0
        else:
            raise ValueError("Query must be a non-empty dictionary")
