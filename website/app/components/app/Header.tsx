import {ReactElement} from "react";
import {Logo} from "~/components/app/Logo";

export function Header():ReactElement {
    return <div className={`
        fixed top-0 left-0 right-0
        bg-white/50 backdrop-blur-sm 
        flex items-center justify-between
        px-3 h-14
    `}>
        <Logo/>
        <div className={"flex items-center gap-2"}>
            <div className={"h-4 w-4 rounded-full bg-green-400 shadow-md"}></div>
            <div className={"h-4 w-4 rounded-full bg-yellow-400 shadow-md"}></div>
            <div className={"h-4 w-4 rounded-full bg-red-400 shadow-md"}></div>
        </div>
    </div>;
}