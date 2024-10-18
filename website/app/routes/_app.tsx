import {Outlet} from "@remix-run/react";
import {Header} from "~/components/app/Header";

export default function (){
    return <div className={"overflow-x-hidden overflow-y-auto overscroll-none scrollbar-hide pt-14"}>
        <Header/>
        <Outlet/>
    </div>
}