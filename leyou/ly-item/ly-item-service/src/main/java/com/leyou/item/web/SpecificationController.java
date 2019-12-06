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
    public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam ("gid") Long gid){
        return ResponseEntity.ok(specService.queryParamByGid(gid));
    }


}
