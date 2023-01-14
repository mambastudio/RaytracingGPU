
// this is where you select the required bsdf, portal for filtering later
__kernel void SetupBSDFRaytrace(global Intersection* isects,
                                global Ray*          rays,
                                global Bsdf*         bsdfs,
                                global Material*     materials)
{
    int global_id = get_global_id(0);

    global Intersection* isect = isects + global_id;
    global Ray* ray            = rays + global_id;
    global Bsdf* bsdf          = bsdfs + global_id;
  
    if(isect->hit)
    {
          *bsdf                = setupBsdf(ray, isect, materials);
    }
}

__kernel void textureInitPassRT(
    global Bsdf*         bsdfs,
    global Intersection* isects,
    global TextureData*  texData
)
{
    //get thread id
    int id = get_global_id( 0 );
    
    //get intersection and material
    global Intersection* isect = isects + id;
    global Bsdf* bsdf          = bsdfs + id;
    global TextureData* tex    = texData + id;
    
    if(isBsdfEmitter(*bsdf))
        return;
        
    tex->parameters.x = bsdf->materialID;                //to find image in CPU during texture lookup search in host code

    //diffuse
    if(bsdf->param.isDiffuseTexture)
    {
      
          tex->diffuseTexture.w = true;
          tex->diffuseTexture.x = castFloatToInt(isect->uv.x);
          tex->diffuseTexture.y = castFloatToInt(isect->uv.y);
    }
    else
    {
          tex->diffuseTexture.w = false;   //no texture
    }

    //glossy
    if(bsdf->param.isGlossyTexture)
    {

          tex->glossyTexture.w = true;
          tex->glossyTexture.x = castFloatToInt(isect->uv.x);
          tex->glossyTexture.y = castFloatToInt(isect->uv.y);
    }
    else
    {
          tex->glossyTexture.w = false;   //no texture
    }
    
    //roughness
    if(bsdf->param.isRoughnessTexture)
    {

          tex->roughnessTexture.w = true;
          tex->roughnessTexture.x = castFloatToInt(isect->uv.x);
          tex->roughnessTexture.y = castFloatToInt(isect->uv.y);
    }
    else
    {
          tex->roughnessTexture.w = false;   //no texture
    }
}

__kernel void updateToTextureColorRT(
    global Bsdf*         bsdfs,
    global TextureData*  texData
)
{
    //get thread id
    int id = get_global_id( 0 );

    //texture for pixel
    global TextureData* tex = texData + id;
    //get bsdf and parameter
    global Bsdf* bsdf          = bsdfs + id;
    
    //diffuse
    if(tex->diffuseTexture.w)
    {
       float4 texColor = getFloat4ARGB(tex->diffuseTexture.z);
       bsdf->param.diffuse_color     = texColor;
    }
    
    //glossy
    if(tex->glossyTexture.w)
    {
       float4 texColor = getFloat4ARGB(tex->glossyTexture.z);
       bsdf->param.glossy_color     = texColor;
    }
    
    //roughness
    if(tex->roughnessTexture.w)
    {
       float4 texColor = getFloat4ARGB(tex->roughnessTexture.z);
       bsdf->param.glossy_param.y  = texColor.x;
       bsdf->param.glossy_param.z  = texColor.y;
    }
}

__kernel void fastShade(
    global Intersection* isects,
    global Bsdf*         bsdfs,
    global int*          imageBuffer
)
{
    //get thread id
    int id = get_global_id( 0 );
    
    //default color
    float4 color = (float4)(0, 0, 0, 1);
    float4 color1 = (float4)(1, 1, 1, 1);
    
    //get intersect
    global Intersection* isect = isects + id;
    
    //get bsdf and parameter
    global Bsdf* bsdf          = bsdfs + id;
    SurfaceParameter param     = bsdf->param;

    if(isect->hit)
    {
        float coeff = bsdf->localDirFix.z;
        color.xyz   = getQuickSurfaceColor(param, coeff).xyz;
    }
    
    imageBuffer[id] = getIntARGB(color);
}

//the heatmap
__kernel void traverseShade(
    global Intersection* isects,
    global int*          imageBuffer
)
{
    //get thread id
    int id = get_global_id( 0 );
    
    //get intersect
    global Intersection* isect = isects + id;

    //gradient color
    float4 c = gradient(min(100, isect->traverseHit) / 100.0f);
    
    //default color
    float4 color = (float4)(c.xyz, 1);
    //apply color
    imageBuffer[id] = getIntARGB(color);
}

__kernel void fastShadeNormals(
    global Intersection* isects,
    global int*          imageBuffer
)
{
    //get thread id
    int id = get_global_id( 0 );
    
    //default color
    float4 color = (float4)(0, 0, 0, 1);
    float4 color1 = (float4)(1, 1, 1, 1);
    
    //get intersect
    global Intersection* isect = isects + id;


    if(isect->hit)
    {
        color.x = (isect->n.x + 1)/2;
        color.y = (isect->n.y + 1)/2;
        color.z = (isect->n.z + 1)/2;
        imageBuffer[id] = getIntARGB(color);
    }
}

__kernel void fastShadeTextureUV(
    global Intersection* isects,
    global int*          imageBuffer
)
{
    //get thread id
    int id = get_global_id( 0 );
    
    //default color
    float4 color = (float4)(0, 0, 0, 1);
    float4 color1 = (float4)(1, 1, 1, 1);
    
    //get intersect
    global Intersection* isect = isects + id;


    if(isect->hit)
    {
        color.x = isect->uv.x;
        color.y = isect->uv.y;
        imageBuffer[id] = getIntARGB(color);
    }
}

__kernel void fastShadeMaterial(
    global Intersection* isects,
    global int*          imageBuffer
)
{
    //get thread id
    int id = get_global_id( 0 );
    
    //default color
    float4 color = (float4)(0, 0, 0, 1);
    float4 color1 = (float4)(1, 1, 1, 1);
    
    //get intersect
    global Intersection* isect = isects + id;


    if(isect->hit)
    {
        float3 hashMatColor3 = hashAndColor(isect->mat + 1);
        float4 hashMatColor4 = (float4)(hashMatColor3, 1.f);
        imageBuffer[id] = getIntARGB(hashMatColor4);
    }
}

__kernel void backgroundShade(
    global Intersection*      isects,
    global CameraStruct*      camera,
    global int*               imageBuffer,
    global Ray*               rays,

    global float4*            envmap,
    global int3*              envmapSize)
{
    //get thread id
    int id = get_global_id( 0 );

    //updated the intersected areas color
    global Intersection* isect = isects + id;
    global Ray* ray            = rays + id;

    if(!isect->hit)
    {
       //update
       if(envmapSize->z > 0)    //is present
       {       
            int envIndex = getSphericalGridIndex(envmapSize->x, envmapSize->y, ray->d);
            float4 envColor = envmap[envIndex];
            gammaFloat4(&envColor, 2.2f);
            imageBuffer[id] = getIntARGB(envColor);
       }
       else
            imageBuffer[id] = getIntARGB((float4)(0, 0, 0, 1));
    }
}

__kernel void updateGroupbufferShadeImage(
    global Intersection* isects,
    global CameraStruct* camera,
    global int* groupBuffer
)
{
    int id= get_global_id( 0 );

    global Intersection* isect = isects + id;
    if(isect->hit)
    {
        groupBuffer[id] = getMaterial(isect->mat);
    }  
    else
        groupBuffer[id] = -1;
}