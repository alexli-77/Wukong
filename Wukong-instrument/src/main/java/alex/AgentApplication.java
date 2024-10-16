package alex;

import alex.transformers.WukongTransformerImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.instrument.Instrumentation;

public class AgentApplication {
    static final Logger logger = LoggerFactory.getLogger(AgentApplication.class);
    private static Instrumentation instrumentation;
    public static void premain(String agentOps, Instrumentation inst) {
        System.out.println("=========premain方法执行========");
        instrumentation = inst;
        //System.out.println(agentOps);
        instrumentation.addTransformer(new WukongTransformerImp());
        System.out.println("Im finished");
    }

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        logger.info("agentmain method invoked with args: {} and inst: {}", args, inst);
        System.out.println("agentmain method invoked with args: {} and inst: {} " + args + " " + inst);
        instrumentation = inst;
        instrumentation.addTransformer(new WukongTransformerImp());
    }

}
