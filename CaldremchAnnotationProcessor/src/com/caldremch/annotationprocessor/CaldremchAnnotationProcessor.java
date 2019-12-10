package com.caldremch.annotationprocessor;

import com.alibaba.fastjson.JSON;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author Caldremch
 * @date 2019-12-10 15:08
 * @email caldremch@163.com
 * @describe
 **/
@SupportedAnnotationTypes("com.caldremch.annotation.NeedLogin")
public class CaldremchAnnotationProcessor extends AbstractProcessor {

    private Filer filer;

    private int round;

    private Elements mElementsUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer =  processingEnvironment.getFiler();
        mElementsUtils = processingEnvironment.getElementUtils();
    }


    //上面注解写死了SupportedAnnotationTypes com.caldremch.annotation.NeedLogin
    /** @Override
    public Set<String> getSupportedAnnotationTypes() {
         * Set<String> annotataions = new LinkedHashSet<String>();
         *         annotataions.add(CustomAnnotation.class.getCanonicalName());
         *         return annotataions;

    }*/

    //支持最新的 java版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

        Messager messager =  processingEnv.getMessager();

        if (round == 0){


        }

//        messager.printMessage(Diagnostic.Kind.ERROR,"process start");



        //如果循环处理完成返回true，否则返回false
       if(env.processingOver()){
           if (!annotations.isEmpty()){
               messager.printMessage(Diagnostic.Kind.ERROR,
                       "Unexpected processing state: annotations still available after processing over");
               return false;
           }
       }

        round++;

//        messager.printMessage(Diagnostic.Kind.ERROR,"round="+round);

        if (annotations.isEmpty()) {
            return false;
        }

        for (TypeElement annotation :annotations) {
            //返回被指定注解类型注解的元素集合。
            Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
            messager.printMessage(Diagnostic.Kind.WARNING,"size->"+elements.size());

            for (Element element:elements){
                if (element.getKind() != ElementKind.CLASS){

                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "only class can be annotation with com.caldremch.annotation.NeedLogin",
                            element
                            );
                }else{

                    //获取包名
                    PackageElement packageElement = mElementsUtils.getPackageOf(element);
                    String pkName = packageElement.getQualifiedName().toString();

                    messager.printMessage(Diagnostic.Kind.WARNING, "包名:"+pkName);


                    //Context 类做为方法参数
                    ClassName context = ClassName.get("android.content","Context");
                    //参数(Context context)
                    ParameterSpec contextParam = ParameterSpec.builder(context, "context").build();


                    MethodSpec methodSpec =  MethodSpec.methodBuilder("process")
                            .addModifiers(Modifier.ABSTRACT,Modifier.PUBLIC)// 接口必须要两个 // modifiers [public] must contain one of [abstract, static, default]
                            .addParameter(contextParam)
                            .build();

                    //生成一个类
                    TypeSpec typeSpec =  TypeSpec.interfaceBuilder("INeedLoginProcessor")
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodSpec)
                            .build();

                    JavaFile file = JavaFile.builder(pkName, typeSpec).build();
                    try {
                        file.writeTo(filer);
                        messager.printMessage(Diagnostic.Kind.WARNING,"INeedLoginProcessor 生成");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }

        }

        return true;
    }
}
