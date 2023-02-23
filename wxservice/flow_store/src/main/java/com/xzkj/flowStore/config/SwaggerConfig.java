package com.xzkj.flowStore.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile({"dev", "test", "pro"})
public class SwaggerConfig {


    @Bean
    public Docket app_api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/admin/**")).build().groupName("后台接口文档").pathMapping("/")
                .apiInfo(apiInfo("后台接口文档", "文档中可以查询及测试接口调用参数和结果", "1.0"));
    }

    @Bean
    public Docket wap_api() {

//        ParameterBuilder tokenPar = new ParameterBuilder();
//        List<Parameter> pars = new ArrayList<Parameter>();
//        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
//        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/web/**")).build().groupName("web和微信接口文档").pathMapping("/")
                .apiInfo(apiInfo("web和微信接口文档", "web和微信接口文档", "1.0"));
    }

    private ApiInfo apiInfo(String name, String description, String version) {
        ApiInfo apiInfo = new ApiInfoBuilder().title(name).description(description).version(version).build();
        return apiInfo;
    }
}