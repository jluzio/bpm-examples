package com.example.spring.bpm.playground.test;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestTags {

  // TODO: 22/11/2023 Fix issues with test isolation.
  //  Currently tests break when running in group. Possible issue with database.
  public static final String NON_ISOLATED_TEST = "non-isolated-test";

}
