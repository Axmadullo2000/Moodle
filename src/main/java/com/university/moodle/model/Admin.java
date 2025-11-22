    package com.university.moodle.model;

    import lombok.Data;
    import lombok.EqualsAndHashCode;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public class Admin extends User {
        private String department;

        public Admin() {
            super();
        }

    }
