package cz.zcu.kiv.crce.classmodel.processor.tools;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;

public class ClassToolsTest {
        private static String baseDescription =
                        "org/springframework/web/reactive/function/client/WebClient";
        private static String[] baseDescriptionVersions;

        @Before
        public void init() {
                initDecrToOwner();
        }


        public void initDecrToOwner() {
                String withSemi = baseDescription + ";";
                String withIdent = "L" + baseDescription;
                String withSemiIdent = withIdent + ";";
                final String withArg =
                                "(Ljava/lang/String;)Lorg/springframework/web/reactive/function/client/WebClient;";
                final String withEmptyArg =
                                "()Lorg/springframework/web/reactive/function/client/WebClient;";
                final String initLike =
                                "Lorg/springframework/web/reactive/function/client/WebClient$1.<init>-(Lcom/app/demo/service/ApiService;)V-false;";
                final String initWithoutDollarSignAndParams =
                                "Lorg/springframework/web/reactive/function/client/WebClient.<init>-()V-false;";


                baseDescriptionVersions = new String[] {baseDescription, withSemi, withIdent,
                                withSemiIdent, withArg, withEmptyArg, initLike,
                                initWithoutDollarSignAndParams};
        }



        @Test
        @Parameters(name = "Testing the transformation of description/signature into the packageName/Class form (e.q. java/lang/String)")
        public void testDescriptionToOwner() {
                for (String type : baseDescriptionVersions) {
                        assertEquals(baseDescription, ClassTools.descriptionToClassPath(type));
                }
        }
}
