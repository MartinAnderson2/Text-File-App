package persistence;

import org.json.JSONObject;

// Implemented by classes that should be saved and loaded.
// Based on [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo) 
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
