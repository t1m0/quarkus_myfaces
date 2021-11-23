package com.t1m0.quarkus.web;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class BackendClientProcessor {

    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
               BuildProducer<FeatureBuildItem> featureProducer) {

        featureProducer.produce(new FeatureBuildItem("backendClient"));
        AdditionalBeanBuildItem unremovableProducer = AdditionalBeanBuildItem.unremovableOf(UserServiceClient.class);
        additionalBeanProducer.produce(unremovableProducer);
    }

}
