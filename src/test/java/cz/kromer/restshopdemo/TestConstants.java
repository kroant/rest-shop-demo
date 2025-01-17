package cz.kromer.restshopdemo;

import java.util.UUID;

public final class TestConstants {

    private TestConstants() {
    }

    public static final String SQL_CLEANUP = "/sql/cleanup.sql";
    public static final String SQL_COMPLEX_TEST_DATA = "/sql/complex-test-data.sql";

    public static final UUID MILK_1_L_PRODUCT_ID = UUID.fromString("3e752234-0a19-49c0-ba18-cfebf0bb7772");
    public static final UUID MILK_500_ML_PRODUCT_ID = UUID.fromString("10b10895-cce9-48c6-bc8c-7025d0a7fe57");
    public static final UUID CASHEW_NUTS_PRODUCT_ID = UUID.fromString("a3c64d30-cb49-4279-9a83-282a7d0c7669");
}
