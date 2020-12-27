package cz.zcu.kiv.crce.restimpl.indexer.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.AnnotationProcessor;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.BodyProcessor;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.ExceptionHandler;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.MemberProcessor;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.ParameterNameProcessor;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.ParameterRequirementProcessor;


import java.util.Set;

/**
 * Created by ghessova on 28.03.2018.
 */
public class RestApiDefinition {

    @JsonProperty
    private String framework;

    @JsonProperty("resource_annotations")
    private Set<String> resourceAnnotations;

    @JsonProperty("endpoint_annotations")
    private Set<String> endpointAnnotations;

    @JsonProperty("url")
    private Set<AnnotationProcessor> urlProcessors;

    @JsonProperty("produces")
    private Set<AnnotationProcessor> producesProcessors;

    @JsonProperty("consumes")
    private Set<AnnotationProcessor> consumesProcessors;

    @JsonProperty("http_method")
    private Set<AnnotationProcessor> httpMethodProcessors;

    @JsonProperty("endpoint_parameters")
    private Set<AnnotationProcessor> endpointParametersProcessors;

    @JsonProperty("default_parameter_value")
    private Set<AnnotationProcessor> defaultParamValuesProcessors;

    @JsonProperty("parameter_name")
    private ParameterNameProcessor parameterNameProcessor;

    @JsonProperty("parameter_requirement")
    private ParameterRequirementProcessor parameterRequirementProcessor;

    @JsonProperty("default_cookie_value")
    private Set<AnnotationProcessor> defaultCookieValuesProcessors;

    @JsonProperty("cookie_name")
    private ParameterNameProcessor cookieNameProcessor;

    @JsonProperty("default_header_value")
    private Set<AnnotationProcessor> defaultHeaderValuesProcessors;

    @JsonProperty("header_name")
    private ParameterNameProcessor headerNameProcessor;

    @JsonProperty("body")
    private BodyProcessor bodyProcessor;

    @JsonProperty("parameter_bean_annotations")
    private Set<String> parameterBeanAnnotations;

    @JsonProperty("generic_response_classes")
    private Set<String> genericResponseClasses;

    @JsonProperty("status_setting_methods")
    private MemberProcessor statusSettingMethods;

    @JsonProperty("entity_setting_methods")
    private Set<MemberProcessor> entitySettingMethods;

    @JsonProperty("status_fields")
    private MemberProcessor statusFields;

    @JsonProperty("cookie_class")
    private String cookieClass;

    @JsonProperty("cookie_setting_method")
    private MemberProcessor cookieSettingMethod;

    @JsonProperty("header_setting_method")
    private MemberProcessor headerSettingMethod;

    @JsonProperty("default_object_mime")
    private String defaultObjectMime;

    @JsonProperty("default_primitive_mime")
    private String defaultPrimitiveMime;

    private boolean subresources;

    private boolean fieldParamAnnotations;
    private boolean fieldParamSetterRequired;

    private String notNullAnnotation = "javax/validation/constraints/NotNull";

    @JsonProperty("exception_handler")
    private ExceptionHandler exceptionHandler;

    @JsonProperty("default_http_methods")
    private Set<String> defaultHttpMethods;

    public boolean supportSubresources() {
        return subresources;
    }

    public void setSubresources(boolean subresources) {
        this.subresources = subresources;
    }

    public Set<String> getGenericResponseClasses() {
        return genericResponseClasses;
    }

    public void setGenericResponseClasses(Set<String> genericResponseClasses) {
        this.genericResponseClasses = genericResponseClasses;
    }

    public Set<String> getParameterBeanAnnotations() {
        return parameterBeanAnnotations;
    }

    public void setParameterBeanAnnotations(Set<String> parameterBeanAnnotations) {
        this.parameterBeanAnnotations = parameterBeanAnnotations;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public Set<String> getResourceAnnotations() {
        return resourceAnnotations;
    }

    public void setResourceAnnotations(Set<String> resourceAnnotations) {
        this.resourceAnnotations = resourceAnnotations;
    }

    public Set<String> getEndpointAnnotations() {
        return endpointAnnotations;
    }

    public void setEndpointAnnotations(Set<String> endpointAnnotations) {
        this.endpointAnnotations = endpointAnnotations;
    }

    public Set<AnnotationProcessor> getUrlProcessors() {
        return urlProcessors;
    }

    public void setUrlProcessors(Set<AnnotationProcessor> urlProcessors) {
        this.urlProcessors = urlProcessors;
    }

    public Set<AnnotationProcessor> getProducesProcessors() {
        return producesProcessors;
    }

    public void setProducesProcessors(Set<AnnotationProcessor> producesProcessors) {
        this.producesProcessors = producesProcessors;
    }

    public Set<AnnotationProcessor> getConsumesProcessors() {
        return consumesProcessors;
    }

    public void setConsumesProcessors(Set<AnnotationProcessor> consumesProcessors) {
        this.consumesProcessors = consumesProcessors;
    }

    public Set<AnnotationProcessor> getHttpMethodProcessors() {
        return httpMethodProcessors;
    }

    public void setHttpMethodProcessors(Set<AnnotationProcessor> httpMethodProcessors) {
        this.httpMethodProcessors = httpMethodProcessors;
    }

    public Set<AnnotationProcessor> getEndpointParametersProcessors() {
        return endpointParametersProcessors;
    }

    public void setEndpointParametersProcessors(Set<AnnotationProcessor> endpointParametersProcessors) {
        this.endpointParametersProcessors = endpointParametersProcessors;
    }

    public ParameterNameProcessor getParameterNameProcessor() {
        return parameterNameProcessor;
    }

    public void setParameterNameProcessor(ParameterNameProcessor parameterNameProcessor) {
        this.parameterNameProcessor = parameterNameProcessor;
    }

    public Set<AnnotationProcessor> getDefaultParamValuesProcessors() {
        return defaultParamValuesProcessors;
    }

    public void setDefaultParamValuesProcessors(Set<AnnotationProcessor> defaultParamValuesProcessors) {
        this.defaultParamValuesProcessors = defaultParamValuesProcessors;
    }

    public Set<AnnotationProcessor> getDefaultCookieValuesProcessors() {
        return defaultCookieValuesProcessors;
    }

    public void setDefaultCookieValuesProcessors(Set<AnnotationProcessor> defaultCookieValuesProcessors) {
        this.defaultCookieValuesProcessors = defaultCookieValuesProcessors;
    }

    public ParameterNameProcessor getCookieNameProcessor() {
        return cookieNameProcessor;
    }

    public void setCookieNameProcessor(ParameterNameProcessor cookieNameProcessor) {
        this.cookieNameProcessor = cookieNameProcessor;
    }

    public Set<AnnotationProcessor> getDefaultHeaderValuesProcessors() {
        return defaultHeaderValuesProcessors;
    }

    public void setDefaultHeaderValuesProcessors(Set<AnnotationProcessor> defaultHeaderValuesProcessors) {
        this.defaultHeaderValuesProcessors = defaultHeaderValuesProcessors;
    }

    public ParameterNameProcessor getHeaderNameProcessor() {
        return headerNameProcessor;
    }

    public void setHeaderNameProcessor(ParameterNameProcessor headerNameProcessor) {
        this.headerNameProcessor = headerNameProcessor;
    }

    public BodyProcessor getBodyProcessor() {
        return bodyProcessor;
    }

    public void setBodyProcessor(BodyProcessor bodyProcessor) {
        this.bodyProcessor = bodyProcessor;
    }

    public MemberProcessor getStatusSettingMethods() {
        return statusSettingMethods;
    }

    public void setStatusSettingMethods(MemberProcessor statusSettingMethods) {
        this.statusSettingMethods = statusSettingMethods;
    }

    public Set<MemberProcessor> getEntitySettingMethods() {
        return entitySettingMethods;
    }

    public void setEntitySettingMethods(Set<MemberProcessor> entitySettingMethods) {
        this.entitySettingMethods = entitySettingMethods;
    }

    public MemberProcessor getStatusFields() {
        return statusFields;
    }

    public void setStatusFields(MemberProcessor statusFields) {
        this.statusFields = statusFields;
    }

    public String getCookieClass() {
        return cookieClass;
    }

    public void setCookieClass(String cookieClass) {
        this.cookieClass = cookieClass;
    }

    public MemberProcessor getCookieSettingMethod() {
        return cookieSettingMethod;
    }

    public void setCookieSettingMethod(MemberProcessor cookieSettingMethod) {
        this.cookieSettingMethod = cookieSettingMethod;
    }

    public MemberProcessor getHeaderSettingMethod() {
        return headerSettingMethod;
    }

    public void setHeaderSettingMethod(MemberProcessor headerSettingMethod) {
        this.headerSettingMethod = headerSettingMethod;
    }

    public String getNotNullAnnotation() {
        return notNullAnnotation;
    }

    public void setNotNullAnnotation(String notNullAnnotation) {
        this.notNullAnnotation = notNullAnnotation;
    }

    public String getDefaultObjectMime() {
        return defaultObjectMime;
    }

    public void setDefaultObjectMime(String defaultObjectMime) {
        this.defaultObjectMime = defaultObjectMime;
    }

    public String getDefaultPrimitiveMime() {
        return defaultPrimitiveMime;
    }

    public void setDefaultPrimitiveMime(String defaultPrimitiveMime) {
        this.defaultPrimitiveMime = defaultPrimitiveMime;
    }

    public ParameterRequirementProcessor getParameterRequirementProcessor() {
        return parameterRequirementProcessor;
    }

    public void setParameterRequirementProcessor(ParameterRequirementProcessor parameterRequirementProcessor) {
        this.parameterRequirementProcessor = parameterRequirementProcessor;
    }

    public boolean isFieldParamAnnotations() {
        return fieldParamAnnotations;
    }

    public void setFieldParamAnnotations(boolean fieldParamAnnotations) {
        this.fieldParamAnnotations = fieldParamAnnotations;
    }

    public boolean isFieldParamSetterRequired() {
        return fieldParamSetterRequired;
    }

    public void setFieldParamSetterRequired(boolean fieldParamSetterRequired) {
        this.fieldParamSetterRequired = fieldParamSetterRequired;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public Set<String> getDefaultHttpMethods() {
        return defaultHttpMethods;
    }

    public void setDefaultHttpMethods(Set<String> defaultHttpMethods) {
        this.defaultHttpMethods = defaultHttpMethods;
    }
}