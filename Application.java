public class Application {
    
    public static void main(String[] args) throws LifecycleException
    {

        int port = 8080;
        String contextPath = "/";
        String servletName = "appServlet";


        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();


        Host host = tomcat.getHost();
        host.setAppBase(new File("src/main").getAbsolutePath());
        host.setAutoDeploy(true);
        host.setDeployOnStartup(true);

        // web.xml
        Context ctx = tomcat.addContext(contextPath, "");
        Tomcat.initWebappDefaults(ctx);

        ctx.setLoader(new WebappLoader(Thread.currentThread().getContextClassLoader()));

        Wrapper appServlet = tomcat.addServlet(contextPath, servletName, new DispatcherServlet(new AnnotationConfigWebApplicationContext(){{
			register(ApplicationConfig.class);
		}}));

        appServlet.setAsyncSupported(true);
		appServlet.setLoadOnStartup(1);
		ctx.addServletMappingDecoded(contextPath, servletName);

        tomcat.start();
        tomcat.getServer().await();
    }
}