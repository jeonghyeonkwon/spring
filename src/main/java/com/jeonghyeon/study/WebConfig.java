package com.jeonghyeon.study;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

@Configuration
//@ComponentScan(useDefaultFilters = false,includeFilters = @ComponentScan.Filter(Controller.class))
@ComponentScan
public class WebConfig {
}
