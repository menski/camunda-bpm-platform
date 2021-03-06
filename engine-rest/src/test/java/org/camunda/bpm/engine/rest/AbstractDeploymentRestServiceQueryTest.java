package org.camunda.bpm.engine.rest;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.camunda.bpm.engine.rest.helper.MockProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public abstract class AbstractDeploymentRestServiceQueryTest extends AbstractRestServiceTest {

  protected static final String DEPLOYMENT_QUERY_URL = TEST_RESOURCE_ROOT_PATH + "/deployment";
  protected static final String DEPLOYMENT_COUNT_QUERY_URL = DEPLOYMENT_QUERY_URL + "/count";
  private DeploymentQuery mockedQuery;

  @Before
  public void setUpRuntimeData() {
    mockedQuery = setUpMockDeploymentQuery(MockProvider.createMockDeployments());
  }

  private DeploymentQuery setUpMockDeploymentQuery(List<Deployment> mockedDeployments) {
    DeploymentQuery sampleDeploymentQuery = mock(DeploymentQuery.class);
    when(sampleDeploymentQuery.list()).thenReturn(mockedDeployments);
    when(sampleDeploymentQuery.count()).thenReturn((long) mockedDeployments.size());
    when(processEngine.getRepositoryService().createDeploymentQuery()).thenReturn(sampleDeploymentQuery);
    return sampleDeploymentQuery;
  }

  @Test
  public void testEmptyQuery() {
    given()
      .then().expect().statusCode(Status.OK.getStatusCode())
      .when().get(DEPLOYMENT_QUERY_URL);
  }

  @Test
  public void testInvalidSortingOptions() {
    executeAndVerifyFailingSorting("anInvalidSortByOption", "asc", Status.BAD_REQUEST,
        InvalidRequestException.class.getSimpleName(), "Cannot set query parameter 'sortBy' to value 'anInvalidSortByOption'");
    executeAndVerifyFailingSorting("name", "anInvalidSortOrderOption", Status.BAD_REQUEST,
        InvalidRequestException.class.getSimpleName(), "Cannot set query parameter 'sortOrder' to value 'anInvalidSortOrderOption'");
  }

  protected void executeAndVerifySuccessfulSorting(String sortBy, String sortOrder, Status expectedStatus) {
    given().queryParam("sortBy", sortBy).queryParam("sortOrder", sortOrder)
      .then().expect().statusCode(expectedStatus.getStatusCode())
      .when().get(DEPLOYMENT_QUERY_URL);
  }

  protected void executeAndVerifyFailingSorting(String sortBy, String sortOrder, Status expectedStatus, String expectedErrorType, String expectedErrorMessage) {
    given().queryParam("sortBy", sortBy).queryParam("sortOrder", sortOrder)
      .then().expect().statusCode(expectedStatus.getStatusCode()).contentType(ContentType.JSON)
      .body("type", equalTo(expectedErrorType))
      .body("message", equalTo(expectedErrorMessage))
      .when().get(DEPLOYMENT_QUERY_URL);
  }

  @Test
  public void testSortByParameterOnly() {
    given().queryParam("sortBy", "name")
      .then().expect().statusCode(Status.BAD_REQUEST.getStatusCode()).contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("Only a single sorting parameter specified. sortBy and sortOrder required"))
      .when().get(DEPLOYMENT_QUERY_URL);
  }

  @Test
  public void testSortOrderParameterOnly() {
    given().queryParam("sortOrder", "asc")
      .then().expect().statusCode(Status.BAD_REQUEST.getStatusCode()).contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("Only a single sorting parameter specified. sortBy and sortOrder required"))
      .when().get(DEPLOYMENT_QUERY_URL);
  }

  @Test
  public void testDeploymentRetrieval() {
    InOrder inOrder = Mockito.inOrder(mockedQuery);

    String queryKey = "Name";
    Response response = given().queryParam("nameLike", queryKey)
        .then().expect().statusCode(Status.OK.getStatusCode())
        .when().get(DEPLOYMENT_QUERY_URL);

    // assert query invocation
    inOrder.verify(mockedQuery).deploymentNameLike(queryKey);
    inOrder.verify(mockedQuery).list();

    String content = response.asString();
    List<String> deployments = from(content).getList("");
    Assert.assertEquals("There should be one deployment returned.", 1, deployments.size());
    Assert.assertNotNull("There should be one deployment returned", deployments.get(0));

    String returnedId = from(content).getString("[0].id");
    String returnedName = from(content).getString("[0].name");
    String returnedDeploymentTime  = from(content).getString("[0].deploymentTime");

    Assert.assertEquals(MockProvider.EXAMPLE_DEPLOYMENT_ID, returnedId);
    Assert.assertEquals(MockProvider.EXAMPLE_DEPLOYMENT_NAME, returnedName);
    Assert.assertEquals(MockProvider.EXAMPLE_DEPLOYMENT_TIME, returnedDeploymentTime);
  }

  @Test
  public void testNoParametersQuery() {
    expect().statusCode(Status.OK.getStatusCode()).when().get(DEPLOYMENT_QUERY_URL);

    verify(mockedQuery).list();
    verifyNoMoreInteractions(mockedQuery);
  }

  @Test
  public void testAdditionalParameters() {
    Map<String, String> queryParameters = getCompleteQueryParameters();

    given().queryParams(queryParameters)
      .expect().statusCode(Status.OK.getStatusCode())
      .when().get(DEPLOYMENT_QUERY_URL);

    // assert query invocation
    verify(mockedQuery).deploymentName(queryParameters.get("name"));
    verify(mockedQuery).deploymentNameLike(queryParameters.get("nameLike"));
    verify(mockedQuery).deploymentId(queryParameters.get("id"));
    verify(mockedQuery).list();
  }

  @Test
  public void testDeploymentBefore() {
    given()
      .queryParam("before", MockProvider.EXAMPLE_DEPLOYMENT_TIME_BEFORE)
    .then().expect()
      .statusCode(Status.OK.getStatusCode())
    .when()
      .get(DEPLOYMENT_QUERY_URL);

    verify(mockedQuery).deploymentBefore(any(Date.class));
    verify(mockedQuery).list();
  }

  @Test
  public void testDeploymentAfter() {
    given()
      .queryParam("after", MockProvider.EXAMPLE_DEPLOYMENT_TIME_AFTER)
    .then().expect()
      .statusCode(Status.OK.getStatusCode())
    .when()
      .get(DEPLOYMENT_QUERY_URL);

    verify(mockedQuery).deploymentAfter(any(Date.class));
    verify(mockedQuery).list();
  }

  private Map<String, String> getCompleteQueryParameters() {
    Map<String, String> parameters = new HashMap<String, String>();

    parameters.put("id", "depId");
    parameters.put("name", "name");
    parameters.put("nameLike", "nameLike");

    return parameters;
  }

  @Test
  public void testSortingParameters() {
    InOrder inOrder = Mockito.inOrder(mockedQuery);
    executeAndVerifySuccessfulSorting("id", "asc", Status.OK);
    inOrder.verify(mockedQuery).orderByDeploymentId();
    inOrder.verify(mockedQuery).asc();

    inOrder = Mockito.inOrder(mockedQuery);
    executeAndVerifySuccessfulSorting("id", "desc", Status.OK);
    inOrder.verify(mockedQuery).orderByDeploymentId();
    inOrder.verify(mockedQuery).desc();

    inOrder = Mockito.inOrder(mockedQuery);
    executeAndVerifySuccessfulSorting("deploymentTime", "asc", Status.OK);
    inOrder.verify(mockedQuery).orderByDeploymenTime();
    inOrder.verify(mockedQuery).asc();

    inOrder = Mockito.inOrder(mockedQuery);
    executeAndVerifySuccessfulSorting("deploymentTime", "desc", Status.OK);
    inOrder.verify(mockedQuery).orderByDeploymenTime();
    inOrder.verify(mockedQuery).desc();

    inOrder = Mockito.inOrder(mockedQuery);
    executeAndVerifySuccessfulSorting("name", "asc", Status.OK);
    inOrder.verify(mockedQuery).orderByDeploymentName();
    inOrder.verify(mockedQuery).asc();

    inOrder = Mockito.inOrder(mockedQuery);
    executeAndVerifySuccessfulSorting("name", "desc", Status.OK);
    inOrder.verify(mockedQuery).orderByDeploymentName();
    inOrder.verify(mockedQuery).desc();
  }

  @Test
  public void testSuccessfulPagination() {
    int firstResult = 0;
    int maxResults = 10;
    given().queryParam("firstResult", firstResult).queryParam("maxResults", maxResults)
      .then().expect().statusCode(Status.OK.getStatusCode())
      .when().get(DEPLOYMENT_QUERY_URL);

    verify(mockedQuery).listPage(firstResult, maxResults);
  }

  /**
   * If parameter "firstResult" is missing, we expect 0 as default.
   */
  @Test
  public void testMissingFirstResultParameter() {
    int maxResults = 10;
    given().queryParam("maxResults", maxResults)
      .then().expect().statusCode(Status.OK.getStatusCode())
      .when().get(DEPLOYMENT_QUERY_URL);

    verify(mockedQuery).listPage(0, maxResults);
  }

  /**
   * If parameter "maxResults" is missing, we expect Integer.MAX_VALUE as default.
   */
  @Test
  public void testMissingMaxResultsParameter() {
    int firstResult = 10;
    given().queryParam("firstResult", firstResult)
      .then().expect().statusCode(Status.OK.getStatusCode())
      .when().get(DEPLOYMENT_QUERY_URL);

    verify(mockedQuery).listPage(firstResult, Integer.MAX_VALUE);
  }

  @Test
  public void testQueryCount() {
    expect().statusCode(Status.OK.getStatusCode())
      .body("count", equalTo(1))
      .when().get(DEPLOYMENT_COUNT_QUERY_URL);

    verify(mockedQuery).count();
  }

}
