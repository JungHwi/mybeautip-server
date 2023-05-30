package com.jocoos.mybeautip.domain.product;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport;

class ProductServiceTest extends RestDocsIntegrationTestSupport {

//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Test
//    void tempSave() {
//        List<FileDto> files = List.of(new FileDto(FileOperationType.UPLOAD, "url"));
//        ProductTempRequest temp = new ProductTempRequest(null, "temp", null, 10, null, files);
//        Product product = productService.tempSave(temp);
//        System.out.println("product = " + product);
//
//        entityManager.flush();
//        entityManager.clear();
//
//        ProductTempRequest temp2 = new ProductTempRequest(4L, "temp2", 10L, 100, null, files);
//        Product product1 = productService.tempSave(temp2);
//        System.out.println("product1 = " + product1);
//
//        entityManager.flush();
//        entityManager.clear();
//    }
//
//    @Test
//    void save() {
//        ProductTempRequest temp = new ProductTempRequest(null, "temp", null, 10, null, null);
//        Product product = productService.tempSave(temp);
//        System.out.println("product = " + product);
//
//        entityManager.flush();
//        entityManager.clear();
//
//        ProductCreateRequest complete = new ProductCreateRequest(5L, "complete", 10L, 100, null);
//        Product save = productService.save(complete);
//        System.out.println("save = " + save);
//
//        entityManager.flush();
//        entityManager.clear();
//    }
//
//    @Test
//    void save2() {
//        ProductCreateRequest complete = new ProductCreateRequest(5L, "complete", 10L, 100, null);
//        Product save = productService.save(complete);
//        System.out.println("save = " + save);
//
//        entityManager.flush();
//        entityManager.clear();
//    }
}
