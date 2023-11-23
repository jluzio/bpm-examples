# Camunda BPMN

## Docs
- https://docs.camunda.org/manual/latest/
  - https://docs.camunda.org/manual/latest/user-guide/
  - https://docs.camunda.org/manual/latest/reference/bpmn20/
- Expression Language
  - https://docs.camunda.org/manual/latest/user-guide/process-engine/expression-language/
- Testing
  - https://docs.camunda.org/manual/latest/user-guide/testing/
  - https://docs.camunda.org/manual/latest/user-guide/testing/assert-examples/
  - https://docs.camunda.org/manual/latest/user-guide/spring-boot-integration/develop-and-test/
    - NOTE: see issues with testing
- Examples
  - https://docs.camunda.org/manual/latest/examples/
    - https://github.com/camunda/camunda-bpm-examples
    - https://github.com/camunda-consulting/camunda-7-code-examples
- Other versions (switch version on a specific page). 
  - Docs for 7.16
    - https://docs.camunda.org/manual/7.16/
      - https://docs.camunda.org/manual/7.16/examples/
      - https://docs.camunda.org/manual/7.16/user-guide/dmn-engine/testing/
  - Examples for 7.16 (use tag)
    - https://github.com/camunda/camunda-bpm-examples/tree/7.16
  - FEEL
    - https://camunda.github.io/feel-scala/docs/reference/
    - https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-expressions-introduction/

## REST API
- https://docs.camunda.org/manual/latest/reference/rest/
- Check Download link:
  - https://docs.camunda.org/rest/camunda-bpm-platform/7.21/
  - https://docs.camunda.org/rest/camunda-bpm-platform/7.19/

## Testing with SpringBoot
https://docs.camunda.org/manual/latest/user-guide/spring-boot-integration/develop-and-test/
~~~java
@SpringBootTest(
  // ...other parameters...
  properties = {
    "camunda.bpm.generate-unique-process-engine-name=true",
    // this is only needed if a SpringBootProcessApplication 
    // is used for the test
    "camunda.bpm.generate-unique-process-application-name=true",
    "spring.datasource.generate-unique-name=true",
    // additional properties...
  }
)
~~~
~~~java
  @Autowired
  ProcessEngine processEngine;  

  @Before
  public void setUp() {
    init(processEngine);
  }
~~~

## Testing with H2
As described in the testing page, should be able to show the database using something like:
~~~
org.h2.tools.Server.createWebServer("-web").start()
~~~

## Issues with testing
The current configuration (standalone with H2) seems to have some issue with running multiple tests depending on tasks.

There is some configuration for SpringBootTest, but I doesn't seem to affect it.

Using awaitability seems to force something that causes the task to work again. Maybe this is a context issue.
~~~
  /**
   * Validates that the task was created and ready
   * <p>TODO: fix issue with asserts</p>
   * <p>TODO: fix issue with <code>task() == null</code> when running all class tests (this doesn't
   * happen when running only one test)</p>
   * <p>NOTE: as workaround: used await (different thread) to wait for it to be ready</p>
   *
   * @param processInstance
   */
  private void waitForTask(ProcessInstance processInstance) {
    await()
        .until(() -> task(processInstance) != null);
    assertThat(task()).isNotNull();
  }
~~~
