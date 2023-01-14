__kernel void InitCameraRayData(
    global CameraStruct* camera,
    global Ray* rays)
{
    //global id and pixel making
    int id= get_global_id( 0 );      

    //pixel value
    float2 pixel = getPixel(id, camera->dimension.x, camera->dimension.y);

    //camera matrix, m = world_to_view, mInv = view_to_world
    transform camera_matrix = camera_transform(camera->position, camera->lookat, camera->up);

    //get global ray
    global Ray* r = rays + id;

    //distance to ndc and then aspect ratio
    float d = 1.0f/tan(radians((*camera).fov)/2.0f);
    float a = camera->dimension.x/camera->dimension.y;

    //direction (px, py, pz, 0) and origin (0, 0, 0, 0)
    r->d = normalize((float4)(a * (2.f * pixel.x/camera->dimension.x - 1), -2.f * pixel.y/camera->dimension.y + 1, -d, 0));
    r->o = 0;  //will be important for global illumination, when we reuse the rays

    //transform to world space
    r->o = transform_point4(camera_matrix.mInv, r->o);
    r->d = transform_vector4(camera_matrix.mInv, r->d);

    //init ray
    initGlobalRay(r, r->o, r->d);

}
