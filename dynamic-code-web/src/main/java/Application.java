import com.zhouhao.dynamic.code.CodeGetterImpl;
import com.zhouhao.dynamic.code.core.DynamicCodeClassLoader;
import com.zhouhao.dynamic.code.core.DynamicCodeManager;
import com.zhouhao.dynamic.code.core.groovy.GroovyCompiler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages =
{ "com.zhouhao"})
public class Application
{
	public static void main(String[] args)
	{
//		System.setProperty("druid.logType", "log4j2");//启用log4j2
//		System.setProperty("spring.config.location",
//				"classpath:druid.properties,classpath:dubbo.properties,classpath:redis.properties"); //设置配置文件
//		System.setProperty("rocketmq.client.log.configFile", "classpath:log4j2.xml");


//		DynamicCodeClassLoader.INSTANCE.setCompiler(new GroovyCompiler());
//		DynamicCodeManager.INSTANCE.setCodeGetter(new CodeGetterImpl());
//		DynamicCodeManager.INSTANCE.manageCodes();
		SpringApplication.run(Application.class, args);
	}
}
