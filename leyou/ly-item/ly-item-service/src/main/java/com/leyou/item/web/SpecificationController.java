package com.leyou.item.web;
import	java.util.List;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List <SpecGroup>> queryGroupByCid(@PathVariable ("cid") Long id){
            return ResponseEntity.ok(specService.queryGroupByCid(id));
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(
            @RequestParam (value = "gid",required =false) Long gid,
            @RequestParam (value = "cid",required =false) Long cid,
            @RequestParam (value = "searching",required =false) Boolean searching
    ){
        return ResponseEntity.ok(specService.queryParamList(gid,cid,searching));
    }


}
