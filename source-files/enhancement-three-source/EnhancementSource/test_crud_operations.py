import unittest
from ModuleJosephDengler import AnimalShelter

class TestAnimalShelter(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        """Runs once before all tests."""
        cls.shelter = AnimalShelter(collection_name='test_animals')

    def test_create(self):
        """Test creating a new record."""
        test_data = {"animal_type": "Dog", "breed": "Labrador", "age": 3}
        result = self.shelter.create(test_data)
        self.assertTrue(result, "Failed to create record")

    def test_read(self):
        """Test reading records."""
        query = {"animal_type": "Dog"}
        result = self.shelter.read(query)
        self.assertIsInstance(result, list, "Read query did not return a list")

    def test_update(self):
        """Test updating records."""
        # Ensure the document exists before updating
        self.shelter.create({"animal_type": "Dog", "breed": "Labrador", "age": 3})
        
        query = {"breed": "Labrador"}
        new_values = {"age": 4}
        result = self.shelter.update(query, new_values)
        self.assertGreaterEqual(result, 1, "Failed to update record")


    def test_delete(self):
        """Test deleting records."""
        query = {"breed": "Labrador"}
        result = self.shelter.delete(query)
        self.assertGreaterEqual(result, 1, "Failed to delete record")

    @classmethod
    def tearDownClass(cls):
        """Runs once after all tests."""
        cls.shelter.collection.drop()

if __name__ == "__main__":
    unittest.main()
