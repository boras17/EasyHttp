import Headers.HttpHeader;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Test;

import redirect.ErrorType;
import redirect.GenericError;

import java.util.List;

public class GenericErrorConverterTest {


    @Test
    public void givenGenericErrorShouldReturnConvertedIntoStringError() {
        GenericError genericError = new GenericError(100,
                List.of(new HttpHeader("some header", "some value"), new HttpHeader("second header", "second ehader value")),
                "SomeExceptionMessage",
                ErrorType.REDIRECT);
        String converted = GenericError.formattedGenericError(genericError);
        System.out.println(converted);
        String expected = "Server response: \n" +
                "Exception msg: SomeExceptionMessage\n" +
                "Response status: 100\n" +
                "Response headers: \n" +
                "\n" +
                "header key: some header\n" +
                "header value: some value\n" +
                "------------------------------\n" +
                "header key: second header\n" +
                "header value: second ehader value\n" +
                "------------------------------";
        Assertions.assertThat(converted)
                .is(new Condition<>(){
                    @Override
                    public boolean matches(String value) {
                        return value.contains(expected) && value.contains("Log date time");
                    }
                });
    }

}
