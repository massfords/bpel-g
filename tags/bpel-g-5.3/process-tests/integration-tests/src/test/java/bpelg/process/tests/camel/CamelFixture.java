package bpelg.process.tests.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.TimeUnit;

/**
 * @author markford
 *         Date: 4/7/12
 */
public class CamelFixture {
    final DefaultCamelContext context = new DefaultCamelContext();

    private final String from;

    public CamelFixture(String from) throws Exception {
        this.from = from;
        addRoute();
        expectedMessageCount(1);
    }

    private void addRoute() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(from).to("mock:sink");
            }
        });
    }

    public void expectedMessageCount(int count) {
        MockEndpoint mock = getMockEndpoint();
        mock.setExpectedMessageCount(1);
    }

    private MockEndpoint getMockEndpoint() {
        return (MockEndpoint) context.getEndpoint("mock:sink");
    }

    public void start() throws Exception {
        context.start();
    }

    public void stop() throws Exception {
        context.stop();
    }

    public void send(String endpoint, String payload) throws Exception {
        Endpoint ep = context.getEndpoint(endpoint);
        Exchange e = ep.createExchange();
        e.setPattern(ExchangePattern.InOnly);
        e.getIn().setBody(payload);
        Producer p = ep.createProducer();
        p.start();
        p.process(e);
    }

    public void assertIsSatisfied() throws InterruptedException {
        assertIsSatisfied(5, TimeUnit.SECONDS);
    }

    public void assertIsSatisfied(int timeout, TimeUnit seconds) throws InterruptedException {
        MockEndpoint.assertIsSatisfied(timeout, seconds, getMockEndpoint());
    }

    public String getReceivedPayload(int offset) {
        String payload = getMockEndpoint().getExchanges().get(offset).getIn().getBody(String.class);
        return payload;
    }

}
