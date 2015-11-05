package com.orbitz.shadow;

import com.orbitz.shadow.model.Request;
import com.orbitz.shadow.transform.Transform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by psundriyal on 11/4/15.
 */

public class ShadowSearchControllerTest {


    HashMap<String,Function> functionMap = new HashMap<>() ;

    Request request = new Request("2015-2-12","ORD","LAS");

    String BOM = "BOM";

    public HashMap<String,Function> buildMap(){
        functionMap.put("changeOrigin",changeOrigin);
        return functionMap;
    }

    @Test
    public void testFunctionChangeOrigin() {
        changeOrigin.apply(request);
        Assert.assertEquals(request.getOrigin(), "BOM");
    }




    @Test
    public void  testBiFunc(){

        Transform.changeDepartureDate.apply(request, "2015-12-25");
        Assert.assertEquals(request.getDeparturteDate(),"2015-12-25");

    }

    @Test
    public void  testToChangeDepartureAheadBehind(){

        Transform.changeDepartureAheadDays.apply(request, "10");
        Assert.assertEquals(request.getDeparturteDate(), "2015-2-22");
        Transform.changeDepartureBehindDays.apply(request, "10");
        Assert.assertEquals(request.getDeparturteDate(), "2015-2-12");
    }


    public Function<Request, Request> changeOrigin = request -> {
        request.setOrigin("BOM");
        return request;
    };


    @FunctionalInterface
    public interface BiFunction<T, U, R> {
        R apply(T t, U u);
    }

}
