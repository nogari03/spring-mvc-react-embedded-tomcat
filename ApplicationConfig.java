@Configuration
@ComponentScan(basePackages = { "${package-path}" }) //ex: com.app
@PropertySources({
		@PropertySource(value = { "${properties-path}" }) 
})
@ImportResource(value = {
		"${xml-path}", // ex: /path1/path2/*.xml
})
public class ApplicationConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(true);
        Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();
        mediaTypes.put("json", MediaType.APPLICATION_JSON);
        mediaTypes.put("xml", MediaType.APPLICATION_XML);
        mediaTypes.put("xml2", MediaType.TEXT_XML);
        mediaTypes.put("xhtml", MediaType.APPLICATION_XHTML_XML);
        mediaTypes.put("html", MediaType.TEXT_HTML);
        configurer.mediaTypes(mediaTypes);
        configurer.defaultContentType(MediaType.TEXT_HTML);
    }


    // 어노테이션 쓰자 일단 넣어둠
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySources = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[] { new ClassPathResource("${resource-path}") }; // ex: propertes file
        propertySources.setLocations(resources);
        propertySources.setIgnoreUnresolvablePlaceholders(true);
        return propertySources;
    }
	
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }
	
    // Resolver Settings
    @Bean
    public BeanNameViewResolver beanNameViewResolver(){
        BeanNameViewResolver viewResolver = new BeanNameViewResolver();
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setOrder(2);
        internalResourceViewResolver.setPrefix("/");
        internalResourceViewResolver.setSuffix(".html");
        return internalResourceViewResolver;
    }

    // Welcome-page Settings
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:http://localhost:3000/");  // dev frontend server
    }


    // Database Settings
    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("${database-url}");
        hikariConfig.setDriverClassName("${database-driver-className}");
        hikariConfig.setUsername("${database-user-name}");
        hikariConfig.setPassword("${database-password}");
        hikariConfig.setMaximumPoolSize(10);
        return hikariConfig;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws Exception {
        DataSource dataSource = new HikariDataSource(hikariConfig());
        return dataSource;
    }

    @Bean
    public SqlSessionFactory dbSqlSessionFactory(DataSource dbDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dbDataSource);
        //sqlSessionFactoryBean.setMapperLocations(resources);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate dbSqlSessionTemplate(SqlSessionFactory dbSqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(dbSqlSessionFactory);
    }
}