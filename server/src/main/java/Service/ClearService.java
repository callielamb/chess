package Service;

import Result.ClearResult;
import dataaccess.Database;

public class ClearService {

    private final Database database;

    public ClearService(Database database) {
        this.database = database;
    }

    public ClearResult clear() {
        database.clear();
        return new ClearResult(null);
    }
}