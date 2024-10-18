export default function (){
    const array=[
        "bg-green-500",
        "bg-blue-500",
        "bg-purple-500",
        "bg-violet-500",
        "bg-red-500",
        "bg-orange-500",
        "bg-amber-500",
        "bg-yellow-500",
    ];

    return <div className={"grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 gap-4 p-4"}>
        {array.map(e=><div key={e} className={`w-full h-64 ${e} rounded-lg shadow-md`}>

        </div>)}
    </div>
}