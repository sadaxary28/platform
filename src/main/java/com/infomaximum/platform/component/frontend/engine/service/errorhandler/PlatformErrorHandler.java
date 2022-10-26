package com.infomaximum.platform.component.frontend.engine.service.errorhandler;

import com.infomaximum.platform.utils.ExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.NestedServletException;

import java.util.List;

public class PlatformErrorHandler extends ErrorHandler {

    private final static Logger log = LoggerFactory.getLogger(PlatformErrorHandler.class);

    private final ActionErrorHandler actionErrorHandler;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public PlatformErrorHandler(
            ActionErrorHandler actionErrorHandler,
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
    ) {
        this.actionErrorHandler = actionErrorHandler;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
                actionErrorHandler.handlerNotFound(response);
            } else if (response.getStatus() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                ResponseEntity<byte[]> responseEntity = actionErrorHandler.handlerServiceUnavailable();

                response.setStatus(responseEntity.getStatusCodeValue());
                responseEntity.getHeaders().forEach((name, values) -> {
                    response.setHeader(name, values.get(0));
                    for (int i = 1; i < values.size(); i++) {
                        response.addHeader(name, values.get(i));
                    }
                });
                response.getOutputStream().write(responseEntity.getBody());

                log.error("SERVICE_UNAVAILABLE", (Throwable) request.getAttribute(Dispatcher.ERROR_EXCEPTION));
            } else if (response.getStatus() >= 400 && response.getStatus() < 500) {
                //Ошибки построения запроса клиентом - игнорируем и прокидываем ответ напрямую
            } else {
                throw (Throwable) request.getAttribute(Dispatcher.ERROR_EXCEPTION);
            }
        } catch (Throwable ex) {
            processingException(ex, baseRequest, response);
        }
    }

    private void processingException(Throwable ex, Request baseRequest, HttpServletResponse response) {
        List<Throwable> chainThrowables = ExceptionUtils.getThrowableList(ex);

        //TODO JDK17
        if (ex instanceof EofException) {
            //Обычный разрыв соединения во время передачи данных
            return;
//        } else if (chainThrowables.size() > 4
//                && chainThrowables.get(3) instanceof FileUploadBase.IOFileUploadException
//                && chainThrowables.get(4) instanceof EofException
//        ) {
//            //Ошибки вида разрыва соединения во время upload файлв
//            return;
//        } else if (chainThrowables.size() == 5
//                && chainThrowables.get(3) instanceof FileUploadException
//                && chainThrowables.get(4) instanceof EofException
//        ) {
//            //Если поставить скорость передачи в 1 кб и сразу отменить передачу
//            return;
//        } else if (chainThrowables.size() == 6
//                && chainThrowables.get(3) instanceof FileUploadException
//                && chainThrowables.get(5) instanceof TimeoutException
//        ) {
//            //Если поставить скорость передачи в 1 кб и через некоторое время отменить передачу
//            return;
//        } else if (chainThrowables.get(chainThrowables.size() - 2) instanceof FileUploadException
//                && chainThrowables.get(chainThrowables.size() - 1) instanceof MultipartStream.MalformedStreamException
//        ) {
//            //Этот exception постоянно кидается в газпромбанке(при большой нагрузки агентов) https://jira.office.infomaximum.com/browse/PLATFORM-7857
//            return;
//        } else if (chainThrowables.get(chainThrowables.size() - 1) instanceof FileUploadException
//                && chainThrowables.get(chainThrowables.size() - 2) instanceof MultipartException
//        ) {
//            //the request was rejected because no multipart boundary was found
//            return;
        } else if (chainThrowables.size() == 3
                && chainThrowables.get(1) instanceof NestedServletException
                && chainThrowables.get(2) instanceof IllegalArgumentException
        ) {
            //Exception в случае невалидного url, например:
            // _build/static/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e//etc/passwd
            return;
        }

        String msgException = "BaseRequest: " + baseRequest.toString() + ", response.status: " + response.getStatus();
        log.error(msgException, ex);

        //Пишем отладочную информацию
        log.error("ChainThrowables, size: {}", chainThrowables.size());
        for (int i = 0; i < chainThrowables.size(); i++) {
            log.error("ChainThrowables, i: {}, exception: {}", i, chainThrowables.get(i).getClass().getName());
        }

        uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), new Exception(msgException, ex));
    }
}
