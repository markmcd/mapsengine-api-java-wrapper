package com.google.api.services;

import static org.easymock.EasyMock.expectLastCall;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

/**
 * Tests for {@link com.google.api.services.HttpRequestInitializerPipeline}.
 */
@PrepareForTest(HttpRequest.class)
@RunWith(PowerMockRunner.class)
public class HttpRequestInitializerPipelineTest {
  @Test
  public void testPipelineExecutesRequestsInSequence() throws Exception {
    // set up the mocks
    // mock controller is required to test ordering across multiple mock objects
    IMocksControl mockController = EasyMock.createStrictControl();
    HttpRequestInitializer firstInitializer =
        mockController.createMock(HttpRequestInitializer.class);
    HttpRequestInitializer secondInitializer =
        mockController.createMock(HttpRequestInitializer.class);
    HttpRequest httpRequest = PowerMock.createMock(HttpRequest.class);

    // ordering matters here.  we want first then second, one execution each.
    firstInitializer.initialize(httpRequest);
    expectLastCall().once();
    secondInitializer.initialize(httpRequest);
    expectLastCall().once();

    mockController.replay();

    // run the code under test
    HttpRequestInitializerPipeline pipeline =
        new HttpRequestInitializerPipeline(firstInitializer, secondInitializer);
    pipeline.initialize(httpRequest);

    // make sure they happened in the correct order
    mockController.verify();
  }

  @Test
  public void testPipelineWithListConstructor() throws Exception {
    // set up the mocks
    // mock controller is required to test ordering across multiple mock objects
    IMocksControl mockController = EasyMock.createStrictControl();
    HttpRequestInitializer firstInitializer =
        mockController.createMock(HttpRequestInitializer.class);
    HttpRequestInitializer secondInitializer =
        mockController.createMock(HttpRequestInitializer.class);
    HttpRequest httpRequest = PowerMock.createMock(HttpRequest.class);

    // ordering matters here.  we want first then second, one execution each.
    firstInitializer.initialize(httpRequest);
    expectLastCall().once();
    secondInitializer.initialize(httpRequest);
    expectLastCall().once();

    mockController.replay();

    // code under test
    HttpRequestInitializerPipeline pipeline =
        new HttpRequestInitializerPipeline(Arrays.asList(firstInitializer, secondInitializer));
    pipeline.initialize(httpRequest);

    // verify
    mockController.verify();
  }
}
