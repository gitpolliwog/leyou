package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page",defaultValue ="1") Integer page,
            @RequestParam(value = "rows",defaultValue ="5") Integer rows,
            @RequestParam(value = "saleable",required =false) Boolean saleable,
            @RequestParam(value = "key",required=false) String key
    ){
            return ResponseEntity.ok(goodsService.querySpuByPage(page,rows,saleable,key));
    }

    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.queryDetailById(id));
    }

    @GetMapping("/sku/list")
    public  ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        return  ResponseEntity.ok(goodsService.querySkuBySpuId(id));
    }


}
