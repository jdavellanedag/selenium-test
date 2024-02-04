package com.unir.plataformas.petclinic;

public class TestConstants {

    public static final String BASE_PATH = "http://localhost:4200/petclinic";
    public static final String VET_PATH = BASE_PATH + "/vets";
    public static final String VET_CREATE_PATH = VET_PATH + "/add";
    public static final String VET_EDIT_PATH = VET_PATH + "/%s/edit";

    enum Specialties {
        RADIOLOGY,
        SURGERY,
        DENTISTRY
    }

}
